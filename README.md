# Game Database
<img align="left" width="128" height="128"  src="https://i.imgur.com/qkYd9tc.png" alt="">  

<br>
A Kotlin app made using the best practices, most recent Jetpack libraries, and Kotlin coroutines. 


<br><br><br><br><br><br>

![Dark Theme](https://i.imgur.com/8JG1fDt.png)
![Light Theme](https://i.imgur.com/2Bul95F.png)



## Code Structure

Game Database's repository supports different configurations but by default, it treats network server's data as a rapidly-changing live data. 
Therefore, it fetches the data at short intervals and caches them if it detects any differences. 
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



## License
[APACHE 2.0](https://apache.org/licenses/LICENSE-2.0)
