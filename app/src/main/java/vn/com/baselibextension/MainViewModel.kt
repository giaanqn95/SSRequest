package vn.com.baselibextension

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import vn.com.baselibextension.InjectContext.retrofitService
import vn.com.baselibextension.base.Repo
import vn.com.baselibextension.base.Request
import vn.com.baselibextension.base.ResultWrapper
import vn.com.baselibextension.base.TypeRepo
import vn.com.baselibextension.utils.LogCat

/**
 * Created by giaan on 11/18/20.
 * Company: Intelin
 * Email: antranit95@gmail.com
 */
class MainViewModel() : ViewModel() {

    val isSuccess = MutableLiveData<Boolean>()

    fun callSomething() = CoroutineScope(Dispatchers.IO).launch {
        val header: HashMap<String, String> = HashMap()
        header["token"] = ""
        retrofitService.build(
            Repo(
                headers = header,
                url = "user/register/",
                message = "0901169215",
                codeRequired = "200",
                typeRepo = TypeRepo.GET
            ),
            Request<BaseResponse>().work(
                onSuccess = { LogCat.d("onWork 1 - ${it.value.data()} ${it.value.message}") },
                onError = { LogCat.d("onWork 1 - ${it.message}") }
            ).loading {})
    }

    fun mergeFunc() = CoroutineScope(Dispatchers.IO).launch {
        retrofitService.merge(arrayOf(callFirst, callSecond).toMutableList()).setEnd {}.setLoading { }.work(
            onSuccess = { isSuccess.postValue(true) },
            onError = { isSuccess.postValue(false) },
        ).setLoading { LogCat.d("Loading is $it") }.buildMerge()
    }

    val callFirst: suspend () -> ResultWrapper<BaseResponse> = {
        val header: HashMap<String, String> = HashMap()
        header["token"] = ""
        retrofitService.build(
            Repo(
                headers = header,
                url = "user/register/",
                message = "0901169215",
                codeRequired = "USERNAME_2000",
                typeRepo = TypeRepo.GET
            ), Request<BaseResponse>().work(
                onSuccess = { LogCat.d("onWork 1 - ${it.value.data()} ${it.value.message}") },
                onError = { LogCat.d("onWork 1 - ${it.message}") }
            ))
    }

    val callSecond: suspend () -> ResultWrapper<BaseResponse> = {
        val header: HashMap<String, String> = HashMap()
        header["token"] = ""
        retrofitService.build(
            Repo(
                headers = header,
                url = "user/register/",
                message = "0901169215",
                codeRequired = "USERNAME_2000",
                typeRepo = TypeRepo.GET
            ), Request<BaseResponse>().work(
                onSuccess = { LogCat.d("onWork 2 - ${it.value.data()} ${it.value.message}") },
                onError = { LogCat.d("onWork 2 - ${it.message}") }
            ))
    }

    fun repeat(): Flow<ResultWrapper<BaseResponse>> = flow {
        emit(callSecond())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val _stateFlow = MutableStateFlow(BaseResponse())

    @OptIn(ExperimentalCoroutinesApi::class)
    var stateFlow: StateFlow<BaseResponse> = MutableStateFlow(BaseResponse())
}