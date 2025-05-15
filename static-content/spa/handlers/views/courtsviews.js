import Html from "../../dsl/htmlfuns.js";
import pagination from "./pagination.js"
import auxiliaryFuns from "../auxfuns.js"

const { splitIntoHourlySlots } = auxiliaryFuns
const { createPaginationLinks } = pagination
const { div, a, ul, li, p, formRequest, span } = Html

function renderCourtsByClubView(contentHeader, content, courts, cid, skip, limit, hasNext) {
    const currHeader = contentHeader.textContent
    const header = "Courts"

    const courtList = courts.length > 0
        ? ul(
            ...courts.map(court =>
                li(a(court.name, `#clubs/${cid}/courts/${court.crid}`))
            )
        )
        : p("No courts found")

    const navigation = createPaginationLinks(`clubs/${cid}/courts`, Number(skip), Number(limit), hasNext)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(courtList, navigation)
}

function renderCourtDetailsView(contentHeader, content, courtResponse, cid, crid) {
    const header = "Court Info"
    const info =
        ul(
            li(`Name : ${courtResponse.name}`),
            li(a("Club", `#clubs/${cid}`)),
            li(
                span(
                    a("Court Rentals", `#clubs/${cid}/courts/${crid}/rentals`),
                    a("by date", `#clubs/${cid}/courts/${crid}/rentals/search`),
                )
            ),
            li(a("Available Hours", `#clubs/${cid}/courts/${crid}/available_hours`))
        )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCourtRentalsView(contentHeader, content, rentals, cid, crid, skip, limit, hasNext) {
    const baseLink = `clubs/${cid}/courts/${crid}/rentals`

    const currHeader = contentHeader.textContent
    const header = "Rentals"

    const backLink = div(a("Back", `#clubs/${cid}/courts/${crid}`))

    const rentalList = rentals.length > 0
        ? ul(
            ...rentals.map(rental =>
                li(a(`${rental.date.toString()} ${rental.initialHour} to ${rental.finalHour} `, `#clubs/${cid}/courts/${crid}/rentals/${rental.rid}`))
            )
        )
        : p("No rentals found")

    const navigation = createPaginationLinks(baseLink, Number(skip), Number(limit), hasNext)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, rentalList, navigation)
}

function renderAvailableHoursView({
                                      contentHeader,
                                      content,
                                      headerText,
                                      backUrl,
                                      availableHours,
                                      slotUrlBuilder,
                                      highlightHour = null,
                                      highlightLabel = "Selected Hour"
                                  }) {
    const backLink = div(a("Back", backUrl))

    const individualSlots = availableHours.flatMap(interval =>
        splitIntoHourlySlots(interval.start, interval.end)
    )

    const mid = Math.ceil(individualSlots.length / 2)
    const [leftSlots, rightSlots] = [individualSlots.slice(0, mid), individualSlots.slice(mid)]

    const renderSlot = hour =>
        li(
            `${hour.start} `,
            hour.start === highlightHour
                ? p(highlightLabel)
                : a("Rent", slotUrlBuilder(hour))
        )

    const createSlotList = slots => ul(...slots.map(renderSlot))

    const columns = div(
        { className: "columns", style: "display: flex; gap: 20px;" },
        createSlotList(leftSlots),
        createSlotList(rightSlots)
    )

    contentHeader.replaceChildren(headerText)
    content.replaceChildren(backLink, columns)
}


function renderCourtAvailableHoursView(contentHeader, content, availableHours, cid, crid, selectedDate) {
    renderAvailableHoursView({
        contentHeader,
        content,
        headerText: `Available Hours for ${selectedDate}`,
        backUrl: `#clubs/${cid}/courts/${crid}/available_hours`,
        availableHours,
        slotUrlBuilder: hour =>
            `#clubs/${cid}/courts/${crid}/rentals/create?date=${selectedDate}&start=${hour.start}`
    })
}

function renderRentalAvailableFinalHours(contentHeader, content, selectedInitialHour, availableHours, cid, crid, date, start) {
    renderAvailableHoursView({
        contentHeader,
        content,
        headerText: `Available Hours for ${date} from ${start}`,
        backUrl: `#clubs/${cid}/courts/${crid}/available_hours?date=${date}`,
        availableHours,
        highlightHour: selectedInitialHour,
        slotUrlBuilder: hour =>
            `#clubs/${cid}/courts/${crid}/rentals/create?date=${date}&start=${start}&end=${hour.start}`
    })
}

function renderCalendarToSearchAvailableHours(contentHeader, content, cid, crid) {
    const header = "Search Available Hours"
    const handleSubmit = async function(e){
        e.preventDefault()
        const validDate = document.querySelector("#date").value
        window.location.hash = `#clubs/${cid}/courts/${crid}/available_hours?date=${validDate}`
    }

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const children = div(
        a("Back", `#clubs/${cid}/courts/${crid}`),
        formRequest(fields, handleSubmit, {
            className: "form",
            submitText: "Get Available Hours"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children)
}

function renderCreateClubForm(contentHeader, content, cid) {
    const header = "Create Court"
    const handleSubmit = function (e) {
        e.preventDefault()
        const courtName = document.querySelector("#courtName").value
        window.location.hash = `#clubs/${cid}/courts/create?name=${courtName}`
    }

    const fields = [
        { id: "courtName", label: "Name: ", type: "text", required: true, placeholder: "Enter Court Name" },
    ]

    const backLink = div(a("Back", `#clubs/${cid}`))
    const children = li(
        formRequest(fields, handleSubmit, {
            className: "form",
            submitText: "Create Court"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children, backLink)
}

const courtsViews = {
    renderCourtsByClubView,
    renderCourtDetailsView,
    renderCourtRentalsView,
    renderCourtAvailableHoursView,
    renderCalendarToSearchAvailableHours,
    renderRentalAvailableFinalHours,
    renderCreateClubForm
}

export default courtsViews