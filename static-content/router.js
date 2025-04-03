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
    const templateParts = template.split('/')
    const pathParts = path.split('/')

    return templateParts.length === pathParts.length &&
        templateParts.every((part, index) => part.startsWith(':') ||
            part === pathParts[index]);
}

const router = {
    addRouteHandler,
    getRouteHandler,
    addDefaultNotFoundRouteHandler
}

function getUriValues(path, handler){
    const temp = routes.fir


    return
}

export default router