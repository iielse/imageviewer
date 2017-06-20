package ch.ielse.demo.p05.business

import ch.ielse.demo.p05.api.ApiEntity

class BalanceJournalInfo : ApiEntity() {

    internal var data: Wrapper? = null

    internal class Wrapper {
        var balanceList: List<BalanceJournalInfo>? = null
    }

    internal var id: String? = null
    internal var content: String? = null

    @Throws(Exception::class)
    override fun extraReal(): List<BalanceJournalInfo>? {
        return data!!.balanceList
    }
}
