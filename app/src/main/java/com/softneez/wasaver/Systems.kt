package com.softneez.wasaver

class Systems {
    companion object {
        // repository
        private var repository_instance : Repository? = null

        fun getRepo() : Repository {
           return repository_instance ?: kotlin.run {
               val repo = Repository()
               repository_instance = repo
               repo
           }
        }
    }
}