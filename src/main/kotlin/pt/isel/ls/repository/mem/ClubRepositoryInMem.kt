package pt.isel.ls.repository.mem

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.ClubRepository
import pt.isel.ls.repository.mem.UserRepositoryInMem.users

object ClubRepositoryInMem : ClubRepository {
    val clubs = mutableListOf<Club>()

    private var currId = 0u

    override fun createClub(
        name: String,
        ownerId: UInt,
    ) {
        val validName = Name(name)

        require(clubs.all { it.name != validName })

        currId += 1u

        val owner = users.find { it.uid == ownerId }

        requireNotNull(owner)

        val club =
            Club(
                cid = currId,
                name = validName,
                owner = owner,
            )

        clubs.add(club)
    }

    override fun findClubByName(name: String): Club? {
        val validName = Name(name)

        return clubs.firstOrNull { it.name == validName }
    }

    override fun save(element: Club) {
        val findClub = clubs.find { it.cid == element.cid }

        // club exists, so update
        if (findClub != null) {
            clubs.map { club ->
                if (club.cid == element.cid) {
                    element
                } else {
                    club
                }
            }
        } else {
            // add element to the user list
            clubs.add(element)
        }
    }

    override fun findByIdentifier(id: UInt): Club? = clubs.firstOrNull { it.cid == id }

    override fun findAll(): List<Club> = clubs

    override fun deleteByIdentifier(id: UInt) {
        clubs.removeIf { it.cid == id }
    }
}