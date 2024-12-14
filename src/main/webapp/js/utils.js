function objectToUrlFormEncoded(parameters) {
    // https://stackoverflow.com/a/37562814/4951015
    // Code could be simplified if support for HTMLUnit is dropped
    // body: new URLSearchParams(parameters) is enough then, but it doesn't work in HTMLUnit currently
    let formBody = [];
    for (const property in parameters) {
        const encodedKey = encodeURIComponent(property);
        const encodedValue = encodeURIComponent(parameters[property]);
        formBody.push(encodedKey + "=" + encodedValue);
    }
    return formBody.join("&");
}
