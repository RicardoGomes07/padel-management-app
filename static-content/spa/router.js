const routes = []

let notFoundRouteHandler = () => { throw "Route handler for unknown routes not defined" }

function addRouteHandler(pathTemplate, handler){
    routes.push({pathTemplate, handler})
}

function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

function getRouteHandler(path){
    const route = routes.find(r => matchPathTemplate(r.pathTemplate, path));
    return route ? route.handler : notFoundRouteHandler
}

function matchPathTemplate(template, path) {
    const [pathWithoutQuery] = path.split('?');
    const templateParts = template.split('/');
    const pathParts = pathWithoutQuery.split('/');

    return templateParts.length === pathParts.length &&
        templateParts.every((part, index) => part.startsWith(':') || part === pathParts[index]);
}


export const router = {
    addRouteHandler,
    getRouteHandler,
    addDefaultNotFoundRouteHandler,
}

export const request = {
    getRequestArgs,
    cleanArgs,
    path,
    query,
}

let args = {
    path: {},
    query: {}
}

function path(parameter){
    return args.path[parameter] ? args.path[parameter] : null
}

function query(parameter){
    return args.query[parameter] ? args.query[parameter] : null
}

function cleanArgs(){
    args =  { path: {}, query: {} }
}

function getRequestArgs(handler, path) {
    const template = routes.find(r => r.handler === handler)?.pathTemplate || null;
    if (!template) return

    const pathParts = path.split("/")
    const templateParts = template.split("/")

    templateParts.forEach((part, index) => {
        if (part.startsWith(":")) args.path[part.substring(1)] = pathParts[index] || null
    })

    const queryString = path.split("?")[1] || ""
    queryString.split("&").filter(Boolean).forEach(entry => {
        const [key, value] = entry.split("=")
        if (key) args.query[key] = value || null
    })
}
