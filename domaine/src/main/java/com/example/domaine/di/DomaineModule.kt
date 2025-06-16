package com.example.domaine.di

import com.example.domaine.usescase.findDelivery.FindDeliveryUsesCase
import com.example.domaine.usescase.findDelivery.FindDeliveryUsesCaseImp
import dagger.Binds

abstract class DomaineModule {
    @Binds
    abstract fun provideFindDeliveryUsesCase(findDeliveryUsesCaseImp: FindDeliveryUsesCaseImp):FindDeliveryUsesCase
}