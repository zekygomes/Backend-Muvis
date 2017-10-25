package br.unifor.muvis.entity

import sun.management.Agent

data class Actor(val id: Long, val name: String, val age: Agent, val movies: Set<Movie>)