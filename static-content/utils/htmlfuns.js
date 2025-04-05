function createElement(tag, props, ...children) {
    const element = document.createElement(tag)

    Object.entries(props).forEach(([key, value]) => {
        if (key.startsWith("on") && typeof value === "function") {
            element.addEventListener(key.slice(2).toLowerCase(), value)
        } else {
            element[key] = value;
        }
    })

    children.forEach(child => {
        if (typeof child === "string" || typeof child === "number") {
            element.appendChild(document.createTextNode(child))
        } else if (child instanceof Node) {
            element.appendChild(child)
        }
    })

    return element
}


function div(...children) {
    return createElement("div", {},...children)
}

function a(text, href) {
    return createElement("a", {textContent:text, href: href})
}

function ul(...children) {
    return createElement("ul",{},...children)
}

function li(...children) {
    return createElement("li", {}, ...children)
}

function h1(text) {
    return createElement("h1", {textContent:text})
}

function h2(text) {
    return createElement("h2", {textContent:text})
}

const Html = {
    div,
    a,
    ul,
    li,
    h1,
    h2
}

export default Html;
