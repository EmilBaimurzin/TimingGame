package com.timing.game.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timing.game.core.library.random
import com.timing.game.domain.game.Color
import com.timing.game.domain.game.GameItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(private val notBotsList: List<Color>) : ViewModel() {
    private val _redScores = MutableLiveData(0)
    val redScores: LiveData<Int> = _redScores

    private val _blueScores = MutableLiveData(0)
    val blueScores: LiveData<Int> = _blueScores

    private val _greenScores = MutableLiveData(0)
    val greenScores: LiveData<Int> = _greenScores

    private val _yellowScores = MutableLiveData(0)
    val yellowScores: LiveData<Int> = _yellowScores

    private val _timer = MutableLiveData(60)
    val timer: LiveData<Int> = _timer

    var callBack: ((Color) -> Unit)? = null
    var callBackSound: (() -> Unit)? = null
    private var isShuffling = false
    private var isClicked = false
    private var gameScope = CoroutineScope(Dispatchers.Default)
    var gameState = true
    var pauseState = false

    private val _imageValue = MutableLiveData(GameItem.SYMBOL_1)
    val imageValue: LiveData<GameItem> = _imageValue

    private fun startTimer() {
        gameScope.launch {
            var value = _timer.value!!
            while (value != 0) {
                _timer.postValue(value - 1)
                value -= 1
                delay(1000)
            }
        }
    }

    fun endTimer() {
        _timer.postValue( -1 )
    }

    fun startGame() {
        gameState = true
        gameScope = CoroutineScope(Dispatchers.Default)
        startTimer()
        generateImages()
    }

    fun stopGame() {
        gameState = false
        gameScope.cancel()
    }

    private fun generateImages() {
        gameScope.launch {
            while (true) {
                callBackSound?.invoke()
                repeat(30) {
                    isShuffling = true
                    val randomValue = listOf(
                        GameItem.SYMBOL_1,
                        GameItem.SYMBOL_2,
                        GameItem.SYMBOL_3,
                        GameItem.SYMBOL_4,
                        GameItem.SYMBOL_5,
                        GameItem.BOMB
                    ).random()
                    _imageValue.postValue(randomValue)
                    delay(100)
                }
                isShuffling = false
                val randomValue = listOf(
                    GameItem.SYMBOL_1,
                    GameItem.SYMBOL_2,
                    GameItem.SYMBOL_3,
                    GameItem.SYMBOL_4,
                    GameItem.SYMBOL_5,
                    GameItem.BOMB
                ).random()
                isClicked = false
                generateClicks()
                _imageValue.postValue(randomValue)
                delay(1500)
            }
        }
    }

    private fun generateClicks() {
        if (!notBotsList.contains(Color.RED)) {
            gameScope.launch {
                if (1 random 2 == 1) {
                    delay((300..500).random().toLong())
                    callBack?.invoke(Color.RED)
                    buttonClick(Color.RED)
                }
            }
        }
        if (!notBotsList.contains(Color.BLUE)) {
            gameScope.launch {
                if (1 random 2 == 1) {
                    delay((300..500).random().toLong())
                    callBack?.invoke(Color.BLUE)
                    buttonClick(Color.BLUE)
                }
            }
        }
        if (!notBotsList.contains(Color.YELLOW)) {
            gameScope.launch {
                if (1 random 2 == 1) {
                    delay((300..500).random().toLong())
                    callBack?.invoke(Color.YELLOW)
                    buttonClick(Color.YELLOW)
                }
            }
        }
        if (!notBotsList.contains(Color.GREEN)) {
            gameScope.launch {
                if (1 random 2 == 1) {
                    delay((300..500).random().toLong())
                    callBack?.invoke(Color.GREEN)
                    buttonClick(Color.GREEN)
                }
            }
        }
    }

    fun buttonClick(value: Color) {
        if (isShuffling) {
            degreeScores(value)
        } else {
            if (isClicked && _imageValue.value!! != GameItem.BOMB) {
                degreeScores(value)
            } else {
                isClicked = true
                increaseScores(value)
            }
        }
    }

    private fun degreeScores(value: Color) {
        when (value) {
            Color.RED -> _redScores.postValue(_redScores.value!! - 5)
            Color.GREEN -> _greenScores.postValue(_greenScores.value!! - 5)
            Color.BLUE -> _blueScores.postValue(_blueScores.value!! - 5)
            Color.YELLOW -> _yellowScores.postValue(_yellowScores.value!! - 5)
        }
    }

    private fun increaseScores(value: Color) {
        val increaseTo = when (_imageValue.value!!) {
            GameItem.SYMBOL_1 -> 5
            GameItem.SYMBOL_2 -> 4
            GameItem.SYMBOL_3 -> 3
            GameItem.SYMBOL_4 -> 2
            GameItem.SYMBOL_5 -> 1
            GameItem.BOMB -> -10
        }
        when (value) {
            Color.RED -> _redScores.postValue(_redScores.value!! + increaseTo)
            Color.GREEN -> _greenScores.postValue(_greenScores.value!! + increaseTo)
            Color.BLUE -> _blueScores.postValue(_blueScores.value!! + increaseTo)
            Color.YELLOW -> _yellowScores.postValue(_yellowScores.value!! + increaseTo)
        }
    }
}

class GameViewModelFactory(private val notBotsList: List<Color>): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(notBotsList) as T
    }
}