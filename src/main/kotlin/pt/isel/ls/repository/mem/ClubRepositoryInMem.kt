package pt.isel.ls.repository.mem

import pt.isel.ls.domain.Club
import pt.isel.ls.domain.Name
import pt.isel.ls.domain.PaginationInfo
import pt.isel.ls.repository.ClubRepository
import pt.isel.ls.repository.mem.UserRepositoryInMem.users
import pt.isel.ls.services.ClubError
import pt.isel.ls.services.ensureOrThrow
import pt.isel.ls.services.getOrThrow

object ClubRepositoryInMem : ClubRepository {
    val clubs = mutableListOf<Club>()

    private var currId = 0u

    override fun createClub(
        name: Name,
        ownerId: UInt,
    ): Club {
        ensureOrThrow(
            condition = clubs.all { it.name != name },
            exception = ClubError.ClubAlreadyExists(name.value),
        )

        currId += 1u

        val owner =
            getOrThrow(ClubError.OwnerNotFound(ownerId)) {
                users.find { it.uid == ownerId }
            }

        val club =
            Club(
                cid = currId,
                name = name,
                owner = owner,
            )

        clubs.add(club)
        return club
    }

    override fun findClubsByName(
        name: Name,
        limit: Int,
        offset: Int,
    ): PaginationInfo<Club> {
        val filteredClubs =
            clubs.filter { club ->
                club.name.value.contains(name.value, ignoreCase = true)
            }
        return PaginationInfo(filteredClubs, filteredClubs.size)
    }

    override fun save(element: Club) {
        clubs.removeIf { it.cid == element.cid }
        currId += 1u
        clubs.add(
            Club(
                cid = currId,
                name = element.name,
                owner = element.owner,
            ),
        )
    }

    override fun findByIdentifier(id: UInt): Club? = clubs.firstOrNull { it.cid == id }

    override fun findAll(
        limit: Int,
        offset: Int,
    ): PaginationInfo<Club>{
        val filteredClubs = clubs.drop(offset).take(limit)
        return PaginationInfo(filteredClubs, filteredClubs.size)
    }

    override fun deleteByIdentifier(id: UInt) {
        clubs.removeIf { it.cid == id }
    }

    override fun clear() {
        clubs.clear()
        currId = 0u
    }

    override fun count(): Int = clubs.size
}
