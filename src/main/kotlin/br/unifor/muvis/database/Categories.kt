package br.unifor.muvis.database

import org.jetbrains.exposed.dao.LongIdTable

object Categories: LongIdTable() {

    val name = varchar("name", 100)
    val description = varchar("description", 250)

}