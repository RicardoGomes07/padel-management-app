import classnames from "../handlers/views/classnames.js";
const { listClassName, listElemClassName, linksClassName,
    textInfoClassName, centerDivClassName, h1ClassName,
    h2ClassName} = classnames

function createElement(tag, props = {}, ...children) {
    const element = document.createElement(tag)

    Object.entries(props).forEach(([key, value]) => {
        if (key.startsWith("on") && typeof value === "function") {
            element.addEventListener(key.slice(2).toLowerCase(), value)
        } else if (key === "className") {
            element.className = value
        } else if (key === "style" && typeof value === "object") {
            Object.assign(element.style, value)
        } else {
            element[key] = value
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
    return createElement("div", { className: centerDivClassName  },...children)
}

function a(text, href, classname=linksClassName) {
    return createElement("a", {textContent:text, href: href, className: classname})
}

function ul(...children) {
    return createElement("ul",{ className: listClassName },...children)
}

function li(...children) {
    return createElement("li", { className: listElemClassName }, ...children)
}

function h1(text, classname = h1ClassName) {
    return createElement("h1", {textContent:text, className: classname})
}

function h2(text, classname = h2ClassName) {
    return createElement("h2", {textContent:text, className: classname})
}

function p(text, classname = textInfoClassName) {
    return createElement("p", {textContent:text, class: classname})
}

const Html = {
    div,
    a,
    ul,
    li,
    h1,
    h2,
    p
}

export default Html;
