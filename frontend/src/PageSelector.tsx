export type PageSelectorProps = {
    page:number
    maxPages:number
    setPage:(value:number) => void
}

export default function PageSelector(props:Readonly<PageSelectorProps>) {

    function getPreviousButton() {
        if(props.page > 0) {
            return(<button onClick={() => props.setPage(props.page-1)}>&lt;&lt;</button>)
        } else {
            return(<button disabled>&lt;&lt;</button>)
        }
    }

    function getNextButton() {
        if(props.page < props.maxPages-1) {
            return(<button onClick={() => props.setPage(props.page+1)}>&gt;&gt;</button>)
        } else {
            return(<button disabled>&gt;&gt;</button>)
        }
    }

    function getPageSelector() {
        const pageNums:number[] = [];

        for (let i:number = 0; i < props.maxPages; i++) {
            pageNums.push(i);
        }

        return(
            <select onChange={e =>
                props.setPage(Number.parseInt(e.target.value))}>
                {pageNums.map(n => {
                    if(n==props.page)
                        return(<option key={"page-option"+n} value={n+""} selected>Page {n+1}</option>)
                    else
                        return(<option key={"page-option"+n} value={n+""}>Page {n+1}</option>)
                })}
            </select>
        )
    }

    return(
        <>
            {getPreviousButton()}
            {getPageSelector()}
            {getNextButton()}
        </>
    )
}