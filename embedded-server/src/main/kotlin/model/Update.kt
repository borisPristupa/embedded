package model

data class Update(val rowData: String)

object UpdateStorage {
    // todo: coroutines logic
    suspend fun update(update: Update) {}
    suspend fun getUpdate(): Update {
        return Update("test string");
    }
}
