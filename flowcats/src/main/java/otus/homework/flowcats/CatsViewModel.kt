package otus.homework.flowcats

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsUiState = MutableStateFlow<Result>(Start)
    val catsUiState: StateFlow<Result> = _catsUiState

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                catsRepository.listenForCatFacts()
                    .catch {
                        _catsUiState.value = otus.homework.flowcats.Error(it)
                    }
                    .collect {
                    _catsUiState.value = Success(it)
                }
            }
        }
    }


}

class CatsViewModelFactory(private val catsRepository: CatsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository) as T
}
