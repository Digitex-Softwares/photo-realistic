import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check permissions here (READ_MEDIA_IMAGES)
        checkPermissionAndStartWorker()
    }

    private fun checkPermissionAndStartWorker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                100
            )
        } else {
            startPhotoWorker()
        }
    }

    private fun startPhotoWorker() {
        val workRequest = PeriodicWorkRequestBuilder<PhotoUploadWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PhotoAutoUpload",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
