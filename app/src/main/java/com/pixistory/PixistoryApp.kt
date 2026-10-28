package com.pixistory

import android.app.Application
import com.pixistory.ai.AiClient
import com.pixistory.data.catalog.CatalogRepository
import com.pixistory.data.pixel.PixelKnowledge
import com.pixistory.data.db.PixistoryDb
import com.pixistory.data.repo.DeviceRepository
import com.pixistory.data.repo.FileStore
import com.pixistory.data.repo.SettingsStore
import com.pixistory.export.Exporter
import com.pixistory.notify.PatchAlerts
import com.pixistory.sync.AuthManager
import com.pixistory.sync.FirebaseGate
import com.pixistory.sync.SyncManager
import com.pixistory.sync.SyncScheduler

class AppContainer(val app: Application) {
    val settings = SettingsStore(app)
    val files = FileStore(app)
    val db = PixistoryDb.build(app)
    val repository = DeviceRepository(app, db, settings, files)
    val knowledge = PixelKnowledge(app)
    val catalog = CatalogRepository(app)
    val auth = AuthManager(app)
    val sync = SyncManager(db, settings, auth, files)
    val ai = AiClient(settings)
    val exporter = Exporter(app, repository)
}

class PixistoryApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseGate.init(this)
        container = AppContainer(this)
        container.auth.refresh()
        PatchAlerts.ensureChannel(this)
        val s = container.settings.current
        if (FirebaseGate.isConfigured) SyncScheduler.applyAutoSync(this, s.autoSync)
        PatchAlerts.apply(this, s.patchAlerts)
    }
}
