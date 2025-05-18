// Auxiliary function to split the available hours into hourly slots
// {start: 1, end:2} => ["01:00"]
// {start: 1, end:3} => ["01:00", "02:00"]
// {start: 1, end:24} => ["01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"]
function splitIntoHourlySlots(start, end) {
    const result = []
    for (let hour = start; hour < end; hour++) {
        result.push(`${hour.toString().padStart(2, "0")}:00`)
    }
    return result
}


// Just a simple function to parse the hour from a string
// This function receives the hour string is in the format "HH:MM" how it is shown in the UI
// and returns the hour as a number (integer)
// For example, "14:00" will return 14 
function parseHourFromString(hourString) {
    const [hour, minute] = hourString.split(":").map(Number)
    return hour
}


function isValidDate(dateString) {
    const date = new Date(dateString)
    return !isNaN(date.getTime())
}

function isValidHour(hourString) {
    const hour = parseInt(hourString, 10)
    return !isNaN(hour) && hour >= 0 && hour <= 23
}

function contains(availableHours, hour) {
    return availableHours.some(range => range.start <= hour && hour <= range.end)
}
function getFinalRentalHours(availableHours, initialHour) {
    return availableHours
        .filter(range => range.start <= initialHour && initialHour <= range.end)
        .map(range => ({ start: initialHour, end: range.end }))[0]
}


const auxiliaryFuns = {
    splitIntoHourlySlots,
    parseHourFromString,
    isValidDate,
    isValidHour,
    contains,
    getFinalRentalHours
}

export default auxiliaryFuns