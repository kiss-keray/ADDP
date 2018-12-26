interface IFetchData {
    method: string,
    data?: object | null | undefined
}
class ApiResponse {
    private e: boolean = false;
    private url: string;
    private data: IFetchData;
    private errorFunc: (data: any) => void;
    public constructor(url: string, data: IFetchData) {
        this.url = url;
        this.data = data;
    }
    public then(resultThen: (data: any) => void): ApiResponse {
        try {
            fetch(this.url, {
                method: this.data.method,
                body: !this.data.data ? null : this.data.data.toString(),
                headers: new Headers({
                    'Content-Type': 'application/x-www-form-urlencoded'
                })
            }).then(result => {
                if (!result.ok) {
                    this.errorFunc(result);
                    this.e = true;
                    return result;
                }
                return result.json();
            }).then(result => {
                if (!this.e) {
                    if (result.success) {
                        resultThen(result.data)
                    } else {
                        this.e = result;
                    }
                }
            }).catch(error => {
                this.errorFunc(error);
            });
        } catch (e) {
            this.errorFunc(e);
        }
        return this;
    }
    public catch(errorFunc: (data: any) => void): ApiResponse {
        this.errorFunc = errorFunc;
        return this;
    }
}
export function Fetch(url: string, data: IFetchData): ApiResponse {
    return new ApiResponse(url, data);
};