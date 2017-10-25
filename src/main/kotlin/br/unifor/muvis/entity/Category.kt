package br.unifor.muvis.entity

import org.jetbrains.exposed.dao.EntityID

data class Category(val int:Long, val name:String, val description:String)