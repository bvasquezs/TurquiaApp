package cl.duoc.turquiaapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cl.duoc.turquiaapp.domain.model.User
import cl.duoc.turquiaapp.domain.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.turquiaDataStore by preferencesDataStore(name = "turquia_storage")


class LocalStorage(private val context: Context) {

    private val usersKey = stringPreferencesKey("users_json")
    private val wordsKey = stringPreferencesKey("words_json")

    val users: Flow<List<User>> =
        context.turquiaDataStore.data.map { JsonMapper.usersFromJson(it[usersKey]) }

    val words: Flow<List<Word>> =
        context.turquiaDataStore.data.map { JsonMapper.wordsFromJson(it[wordsKey]) }

    suspend fun updateUsers(transform: (List<User>) -> List<User>) {
        context.turquiaDataStore.edit { prefs ->
            val current = JsonMapper.usersFromJson(prefs[usersKey])
            prefs[usersKey] = JsonMapper.usersToJson(transform(current))
        }
    }

    suspend fun updateWords(transform: (List<Word>) -> List<Word>) {
        context.turquiaDataStore.edit { prefs ->
            val current = JsonMapper.wordsFromJson(prefs[wordsKey])
            prefs[wordsKey] = JsonMapper.wordsToJson(transform(current))
        }
    }
}
