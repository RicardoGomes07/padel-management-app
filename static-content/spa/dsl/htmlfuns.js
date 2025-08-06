import classnames from "../handlers/views/classnames.js";
const { listClassName, listElemClassName, linksClassName,
    textInfoClassName, centerDivClassName, h1ClassName,
    h2ClassName, selectedLink} = classnames


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

function input(id, type = "text", placeHolder = "", initValue = "", required = false, onChange = () => {}) {
    return createElement("input", {
        id: id,
        type: type,
        value: initValue,
        placeholder: placeHolder,
        className: "input",
        required: required,
        oninput: (event) => onChange(event.target.value),
    })
}

function label(forId, text) {
    return createElement("label", {
        htmlFor: forId,
        className: "form-label"
    }, text)
}

function formElement(fields, submitHandler, formProps = {}) {
    const formElements = [];

    fields.forEach(field => {
        const fieldContainer = div();
        
        const fieldLabel = label(field.id, field.label);
        
        let inputField;

        if (field.type === "hour") {
            inputField = hourSelect(
                field.id,
                field.value || "",
                field.onChange || (() => {}),
                "form-select",
                field.required
            )
        } else {
            inputField = input(
                field.id,
                field.type || "text",
                field.placeholder || "",
                field.value || "",
                field.required,
                field.onChange || (() => {})
            )
        }

    
        fieldContainer.appendChild(fieldLabel);
        fieldContainer.appendChild(inputField);
        
        formElements.push(fieldContainer);
    })

    const submitButton = button(
        formProps.submitText || "Submit",
        () => {}, 
        {
            type: "submit",
            className: "btn btn-primary mt-2",
            id: "submit-button"
        }
    )

    formElements.push(submitButton)

    return createElement("form", {
        className: formProps.className || "form",
        id: formProps.id,
        onSubmit: submitHandler
    }, ...formElements)
}

function button(text, onClick = () => {}, options = {}) {
    return createElement("button", {
        type: options.type || "button",
        className: options.className || "btn",
        id: options.id,
        disabled: options.disabled || false,
        onclick: onClick,
        textContent: text,
        style: options.style || {}
    })
}

function span(...children) {
    return createElement("span", {}, ...children)
    return createElement("span", { className: selectedLink, 'aria-current': 'page' }, ...children)
}

function hourSelect(id, initialValue, onChange = () => {}, className = "form-select", required = false) {
    const options = [];

    for (let hour = 0; hour <= 24; hour++) {
        const value = hour.toString();
        const option = createElement("option", {
            value: value,
            selected: value === initialValue.toString(),
            textContent: hour === 24 ? "23:59" : hour.toString().padStart(2, "0")
        })
        options.push(option)
    }

    return createElement("select", {
        id: id,
        name: id,
        className: className,
        required: required ? true : undefined,
        onchange: (event) => onChange(event.target.value)
    }, ...options)
}

const Html = {
    div,
    a,
    ul,
    li,
    h1,
    h2,
    p,
    formElement,
    input,
    span,
    label,
    button,
}

export default Html;
