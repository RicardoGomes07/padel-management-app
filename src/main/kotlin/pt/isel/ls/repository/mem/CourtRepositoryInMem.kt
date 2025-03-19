package pt.isel.ls.repository.mem

import pt.isel.ls.domain.Court
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.CourtRepository
import pt.isel.ls.repository.mem.ClubRepositoryInMem.clubs

object CourtRepositoryInMem : CourtRepository {
    val courts = mutableListOf<Court>()

    private var currId = 0u

    override fun createCourt(
        name: Name,
        clubId: UInt,
    ): Court {
        val club = clubs.firstOrNull { it.cid == clubId }

        requireNotNull(club)

        currId += 1u

        val court =
            Court(
                crid = currId,
                name = name,
                club = club,
            )

        courts.add(court)
        return court
    }

    override fun findByClubIdentifier(cid: UInt): List<Court> = courts.filter { it.club.cid == cid }

    override fun save(element: Court) {
        courts.removeIf { it.crid == element.crid }
        courts.add(element)
    }

    override fun findByIdentifier(id: UInt): Court? = courts.firstOrNull { it.crid == id }

    override fun findAll(): List<Court> = courts

    override fun deleteByIdentifier(id: UInt) {
        courts.removeIf { it.crid == id }
    }

    override fun clear() {
        courts.clear()
    }
}
