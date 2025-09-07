import Modal from "./Modal.tsx";
import React from "react";

export type ModalConfirmationProps = {
    title:string;
    children: React.ReactNode;
    onClose: ()=>void;
    onConfirm: ()=>void;
}

export default function ModalConfirmation(props:Readonly<ModalConfirmationProps>) {
    return(
        <Modal open={true} title={props.title} onClose={props.onClose}>
            <div style={{display:"flex", flexDirection:"column"}}>
                {props.children}
                <div style={{display:"flex", flexDirection:"row", justifyContent:"flex-end"}}>
                    <button style={{margin:"10px",width:"80px"}} className={"btn-secondary"}
                            onClick={props.onConfirm}>
                        Yes
                    </button>
                    <button style={{margin:"10px",width:"80px"}}  className={"btn-primary"}
                            onClick={props.onClose}>
                        No
                    </button>
                </div>
            </div>
        </Modal>
    )
}