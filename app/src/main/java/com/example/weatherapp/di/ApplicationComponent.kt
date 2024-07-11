package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Inject

@ApplicationScope
@Component(
    modules = [
        DataModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)


    @Component.Factory
    interface Factory{

        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}