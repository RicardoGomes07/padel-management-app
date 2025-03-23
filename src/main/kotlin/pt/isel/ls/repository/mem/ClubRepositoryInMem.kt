package pt.isel.ls.repository.mem

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.ClubRepository
import pt.isel.ls.repository.mem.UserRepositoryInMem.users

object ClubRepositoryInMem : ClubRepository {
    val clubs = mutableListOf<Club>()

    private var currId = 0u

    override fun createClub(
        name: Name,
        ownerId: UInt,
    ): Club {
        require(clubs.all { it.name != name })

        currId += 1u

        val owner = users.find { it.uid == ownerId }

        requireNotNull(owner)

        val club =
            Club(
                cid = currId,
                name = name,
                owner = owner,
            )

        clubs.add(club)
        return club
    }

    override fun findClubByName(name: Name): Club? = clubs.firstOrNull { it.name == name }

    override fun save(element: Club) {
        clubs.removeIf { it.cid == element.cid }
        clubs.add(element)
    }

    override fun findByIdentifier(id: UInt): Club? = clubs.firstOrNull { it.cid == id }

    override fun findAll(
        limit: Int,
        offset: Int,
    ): List<Club> = clubs.drop(offset).take(limit)

    override fun deleteByIdentifier(id: UInt) {
        clubs.removeIf { it.cid == id }
    }

    override fun clear() {
        clubs.clear()
    }
}
