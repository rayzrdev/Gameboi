package dev.rayzr.gameboi.manager

import io.javalin.http.Context
import java.util.*

object RenderManager {
    fun handleRenderReq(context: Context) {
        val matchId = UUID.fromString(context.pathParam("match-id"))
        val stateId = UUID.fromString(context.pathParam("state-id"))

        val render = getMatchRender(matchId, stateId)

        if (render != null) {
            context.contentType("image/png")
                .status(200)
                .result(render)
        } else {
            context.status(404)
        }
    }

    fun getMatchRender(matchId: UUID, stateId: UUID): ByteArray? {
        val match = MatchManager.currentMatches.find { it.id == matchId }

        return match?.renderContext?.state?.get(stateId)
    }
}