# Game Database

A Kotlin app made using the best practices, most recent Jetpack libraries, and Kotlin coroutines.

It uses the [RAWG Video Games Database](https://choosealicense.com/licenses/mit/) to retrieve and display a video game list and game details when clicked. 

![Dark Theme](https://i.ibb.co/0ZVzY3n/dark-theme.png)
![Light Theme](https://i.ibb.co/kSRMqNG/light-theme.png)



## Code Structure

Game Database's repository supports different configurations but by default, it treats to network server's data as a rapidly-changed or modified data. 
Therefore, it fetches the data at short intervals and caches them if it detects any changes. 
It respects the single source of truth architecture and SOLID principles. 
The data is always retrieved through the Room database where it is exposed as a Flow in the repository layer. 
ViewModels collect those Flows and transforms them into LiveDatas. And from there on, the fragments display the data through data-binding.

Every I/O operation is done through coroutines and their lifecycle is managed by the coroutine scopes to avoid memory leaks.



## Utilized Concepts

* Design patterns
* Dependency Injection
* Object-Oriented Programming
* Version Control
* Day/Night Theme
* Data and view binding
* Single Activity  pattern with the Navigation Component




## Used Libraries

* Retrofit
* Room
* HILT
* OkHttp
* GSON
* Flow
* LiveData
* Coroutine
* ViewModel
* Glide
* Timber
* Palette


## Note

**The tests are not written at the moment but of course, in the following days, both the UI and logic will be well tested.**




## License
[APACHE 2.0](https://apache.org/licenses/LICENSE-2.0)
