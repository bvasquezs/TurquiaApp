package cl.duoc.turquiaapp.data.local

import cl.duoc.turquiaapp.domain.model.User
import cl.duoc.turquiaapp.domain.model.Word
import org.json.JSONArray
import org.json.JSONObject

/** Convierte los modelos a JSON (texto) y viceversa para guardarlos en DataStore. */
object JsonMapper {

    fun usersToJson(users: List<User>): String {
        val array = JSONArray()
        users.forEach { u ->
            array.put(
                JSONObject()
                    .put("id", u.id)
                    .put("name", u.name)
                    .put("email", u.email)
                    .put("passwordHash", u.passwordHash)
                    .put("salt", u.salt)
                    .put("childName", u.childName)
                    .put("childAge", u.childAge)
                    .put("photoPath", u.photoPath ?: JSONObject.NULL)
            )
        }
        return array.toString()
    }

    fun usersFromJson(json: String?): List<User> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                User(
                    id = o.getLong("id"),
                    name = o.getString("name"),
                    email = o.getString("email"),
                    passwordHash = o.getString("passwordHash"),
                    salt = o.getString("salt"),
                    childName = o.getString("childName"),
                    childAge = o.getInt("childAge"),
                    photoPath = if (o.isNull("photoPath")) null else o.getString("photoPath")
                )
            }
        }.getOrDefault(emptyList())
    }

    fun wordsToJson(words: List<Word>): String {
        val array = JSONArray()
        words.forEach { w ->
            array.put(
                JSONObject()
                    .put("id", w.id)
                    .put("turkish", w.turkish)
                    .put("spanish", w.spanish)
                    .put("category", w.category)
                    .put("emoji", w.emoji)
                    .put("isFavorite", w.isFavorite)
                    .put("isCustom", w.isCustom)
            )
        }
        return array.toString()
    }

    fun wordsFromJson(json: String?): List<Word> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                Word(
                    id = o.getLong("id"),
                    turkish = o.getString("turkish"),
                    spanish = o.getString("spanish"),
                    category = o.getString("category"),
                    emoji = o.getString("emoji"),
                    isFavorite = o.getBoolean("isFavorite"),
                    isCustom = o.getBoolean("isCustom")
                )
            }
        }.getOrDefault(emptyList())
    }
}
