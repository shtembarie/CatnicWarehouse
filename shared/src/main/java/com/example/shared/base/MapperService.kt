package com.example.shared.base


interface MapperService<Input,Output> {

    fun mapDomainToDTO(input : Input) : Output

     fun mapDomainToDTO(inputList : List<Input>) : List<Output> = inputList.map { domain->
        mapDomainToDTO(domain)
    }
}