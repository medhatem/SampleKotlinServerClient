package com.example.Data
import com.server.com.server.Models.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


object Goal : Table("goal") {
    val id = integer("id").primaryKey().autoIncrement();
    val teamId = (integer("teamId")references  Team.id).nullable()
    val playerId = (integer("playerId")references  Player.id).nullable()
    val periodId = (integer("periodId")references  Period.id).nullable()
}



data class Goals(val id: Int,  val teamId :Int, val playerId : Int, val periodId : Int){
    companion object{
        @JvmStatic
        @Synchronized
        fun addGoal(gameId : Int, teamId : Int, playerId : Int){
            transaction{
                Goal.insert {
                    it[Goal.teamId] = teamId
                    it[Goal.playerId] = playerId
                    it[Goal.periodId] = Periods.lastPeriod(gameId)
                }
            }
        }
        @JvmStatic
        @Synchronized
        fun getGoalsInMatch(idGame: Int, idTeam: Int?) : Int{
            var ret : Int = 0
            var res : Query? = null
            transaction{
                res = Period.select {
                    Period.gameId.eq(idGame)
                }
            }
            for(row in res!!){
                transaction {
                    var resu = Goal.select {
                        Goal.periodId.eq(row[Period.id])and Goal.teamId.eq(idTeam)
                    }
                    for(ro in resu)
                        ret++
                }
            }
            return ret
        }
    }
}

