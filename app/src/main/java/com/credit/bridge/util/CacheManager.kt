
import com.credit.bridge.util.SPCache


object CacheManager {

    var isAgree by SPCache(false)
    var isAuth by SPCache(false)
    var isNewCustomer by SPCache(false)

    var smsCode by SPCache("")
    var token by SPCache("")

    var mobile by SPCache("")

    var isNeedShowPermissionSheet by SPCache(true, mobile + "permission_sheet")

    var isAlreadyShowPanNumberSheet by SPCache(false)


    var afChannel by SPCache("")

    var historyCacheData by SPCache<Set<String>>(setOf())

    var userScore by SPCache(0.0)

    var customKeyName by SPCache("default_value", key = "my_special_key")
}