export type NavigationItemProps = {
    text:string;
    symbolFile:string;
    onClick:()=>void
}

export default function NavigationItem(props:Readonly<NavigationItemProps>) {
    return(
        <button className={"navigation-item"} onClick={props.onClick}>
            <img
                src={props.symbolFile}
                alt={"Icon for " + props.text}
                height="48"
                width="48" />
            <p>
                {props.text}
            </p>
            &nbsp;
        </button>
    )
}