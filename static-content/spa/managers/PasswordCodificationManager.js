export async function hashPassword(password) {
    // generate the key for the deriveBits
    const keyMaterial = await getKeyMaterial(password)

    return getDerivedBits(keyMaterial)
}

async function getKeyMaterial(password) {
    const enc = new TextEncoder()
    return window.crypto.subtle.importKey(
        "raw", // format: raw bytes
        enc.encode(password), // keyData, the password in bytes
        { name: "PBKDF2" }, // algorithm for this key
        false, // extractable, we don't want to be able to export the key
        ["deriveBits"], // keyUsages, what the key can be used to do, only want to be able to derive bits
    )
}

async function getDerivedBits(keyMaterial) {
    const iterations = 100_000
    const keyLength = 256

    const derivedBits = await window.crypto.subtle.deriveBits(
        {
            "name": "PBKDF2",
            salt: new Uint8Array([]), // for simplification, dont add salt
            "iterations": iterations,
            "hash": "SHA-256"
        },
        keyMaterial,
        keyLength,
    )

    // get the hashed password in Base64
    return btoa(String.fromCharCode(...new Uint8Array(derivedBits)))
}