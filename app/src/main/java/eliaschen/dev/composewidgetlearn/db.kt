package eliaschen.dev.composewidgetlearn

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.launch

@Entity(tableName = "event")
data class EventSchema(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: Long,
)

@Dao
interface EventDao {
    @Insert
    suspend fun insert(date: EventSchema)

    @Query("SELECT * FROM event")
    suspend fun get(): List<EventSchema>
}

@Database(entities = [EventSchema::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun DateDao(): EventDao
}

fun getDatabase(context: Context): AppDataBase {
    return Room.databaseBuilder(
        context.applicationContext, AppDataBase::class.java, "db"
    ).fallbackToDestructiveMigration().build()
}

class EventViewModel(private val db: AppDataBase) : ViewModel() {
    private val _event = MutableLiveData<List<EventSchema>>()
    val event: LiveData<List<EventSchema>> get() = _event

    init {
        update()
    }

    fun add(data: EventSchema) {
        viewModelScope.launch {
            db.DateDao().insert(data)
            update()
        }
    }

    fun update() {
        viewModelScope.launch {
            val events = db.DateDao().get()
            _event.postValue(events)
        }
    }
}
