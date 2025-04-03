function div(...children) {
    const div = document.createElement("div");
    children.forEach(child => div.appendChild(child));
    return div;
}

function a(text, href) {
    const a = document.createElement("a");
    a.textContent = text;
    a.href = href;
    return a;
}

function ul(...children) {
    const ul = document.createElement("ul");
    children.forEach(child => ul.appendChild(child));
    return ul;
}

function li(text) {
    const li = document.createElement("li");
    li.textContent = text;
    return li;
}

function h1(text) {
    const h1 = document.createElement("h1");
    h1.textContent = text;
    return h1;
}

function h2(text) {
    const h2 = document.createElement("h2");
    h2.textContent = text;
    return h2;
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