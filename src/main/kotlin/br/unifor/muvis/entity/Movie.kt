package br.unifor.muvis.entity

data class Movie(val id:Long, val name:String, val description:String,
                 val category:Category, val director:Director, val actors:Set<Actor>)