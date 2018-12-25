interface IFetchData{
    method:string,
    data:object
}
export function Fetch(url: string, data: IFetchData): Promise<Response> {
    return fetch(url, {
        method: data.method,
        body: data.data !== undefined ? JSON.stringify(data.data) : null,
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    });
};