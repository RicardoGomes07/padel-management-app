package pt.isel.ls.services

import pt.isel.ls.domain.Court
import pt.isel.ls.domain.Name
import pt.isel.ls.repository.CourtRepository

sealed class CourtError {
    data object CourtNotFound : CourtError()
}

class CourtService(
    private val courtRepo: CourtRepository,
) {
    /**
     * Function that returns all courts in the system
     * @param cid the club identifier
     * @return list of courts
     */
    fun getCourts(cid: UInt): List<Court> = courtRepo.findByClubIdentifier(cid)

    /**
     * Function that returns a court by its identifier
     * @param crid the court identifier
     * @return either the court or an error indicating that the court was not found
     */
    fun getCourtById(crid: UInt): Either<CourtError.CourtNotFound, Court> {
        val court = courtRepo.findByIdentifier(crid) ?: return failure(CourtError.CourtNotFound)
        return success(court)
    }

    /**
     * Function that creates a new court in the system
     * @param name the court name
     * @param clubId the club identifier
     * @return the new court
     */
    fun createCourt(
        name: Name,
        clubId: UInt,
    ): Court = courtRepo.createCourt(name, clubId)
}
