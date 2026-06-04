import com.credit.bridge.remote.bean.CollectDataInfo
import com.credit.bridge.remote.bean.HomeInfo

object HomeSessionState {
    var isAuthed: Boolean = false
    var currentStep: Int = 0
    var homeInfo: HomeInfo? = null

    fun restoreFromCache() {
        if (!isAuthed) {
            isAuthed = CacheManager.isUserVerified
        }
    }

    fun updateCollectInfo(info: CollectDataInfo) {
        currentStep = info.lrksnnsd
        val verified = info.rvazxrtziwtcvrfrkzczx || info.masxqgkeptyuo
        if (verified) {
            setVerified(true)
        } else if (!isAuthed && !CacheManager.isUserVerified) {
            setVerified(false)
        }
    }

    fun setVerified(verified: Boolean) {
        isAuthed = verified
        CacheManager.isUserVerified = verified
    }

    fun clear() {
        isAuthed = false
        currentStep = 0
        homeInfo = null
        CacheManager.isUserVerified = false
    }
}
