package com.timing.game.ui.choice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChoiceViewModel: ViewModel() {
    private val _playersAmount = MutableLiveData(1)
    val playersAmount: LiveData<Int> = _playersAmount

    private val _playerWithColors = MutableLiveData<Map<Int, Int>>(mapOf())
    val playersWithColors: LiveData<Map<Int,Int>> = _playerWithColors

    fun setPlayersAmount(amount: Int) = _playersAmount.postValue(amount)

    fun addPlayerColor(value: Int) {
        if (_playerWithColors.value!!.size != _playersAmount.value!!) {
            val newMap = _playerWithColors.value!!.toMutableMap()
            newMap[newMap.size + 1] = value
            _playerWithColors.postValue(newMap)
        }
    }

    fun resetMap() {
        _playerWithColors.postValue(mapOf())
    }
}