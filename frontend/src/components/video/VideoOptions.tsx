import { ChangeEvent, useEffect, useRef, useState } from "react";
import AddUser from "@components/video/AddUser";
import Share from "@components/video/Share";
import Argola from "@components/utils/Argola";
import { AsyncAction, VideoManager } from "@hooks/useVideoAndAnotations";
import { VisibilityType } from "@hooks/useVideoStore";

export default function VideoOptions({
    manager,
    asyncAction,
    isVisible,
    close
}: {
    manager: VideoManager,
    asyncAction: AsyncAction | undefined,
    isVisible: boolean,
    close: () => void
}) {
    const [ visibleModal, setVisibleModal ] = useState<"info" | "add" | "edit" | "delete">("info");
    const userRef = useRef<string>("");
    const [ videoUpdate, setVideoUpdate ] = useState({ title: "", description: "" });
    const [ argolas, setArgolas ] = useState<{x: number, y: number, amount: undefined[]}>({x: 0, y: 0, amount: []});

    useEffect(() => {
        function onResize() {
            const { clientWidth, clientHeight } = document
                .querySelector("#video-options-modal-" + visibleModal)!;

            setArgolas({
                amount: new Array(Math.floor(clientWidth/30)).fill(undefined),
                x: -clientWidth/2,
                y: -clientHeight/2
            });
        }

        window.addEventListener("resize", onResize);

        onResize();

        return () => {
            window.removeEventListener("resize", onResize);
        }
    }, [visibleModal]);

    useEffect(() => {
        if (asyncAction === undefined)
            setVisibleModal("info");
    }, [asyncAction]);

    useEffect(() => {
        if (isVisible)
            setVideoUpdate({
                title: manager.video!.title,
                description: manager.video!.description
            });
        else {
            setVideoUpdate({
                title: "",
                description: ""
            });
            userRef.current = "";
        }
    }, [isVisible]);

    function updateVideoUpdate(e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        setVideoUpdate(cur => ({...cur, [e.target.id]: e.target.value}));
    }

    return (
        <div
            id="video-options-modal-parent-element"
            className={`absolute h-full w-full bg-black/50 flex items-center justify-center ${isVisible ? "opacity-100 z-1" : "opacity-0 -z-1"}`}
            onClick={e => {
                e.stopPropagation();

                if ((e.target as HTMLDivElement).id !== "video-options-modal-parent-element")
                    return;

                close();
            }}
        >
            <div
                id="video-options-modal-info"
                className="bg-[#ffffdb] h-[58%] w-[65%] p-4 flex flex-col items-center justify-center gap-6 rounded-3xl"
            >
                {argolas.amount.map((_, i) =>
                    <Argola
                        key={i}
                        radio={40}
                        x={i*29 + argolas.x + 45}
                        y={argolas.y}
                    />
                )}
                <div className="absolute top-[calc(21%_+_24px)] right-[calc(17.5%_+_24px)]">
                    <button onClick={close}>
                        <img
                            src="https://images.icon-icons.com/2518/PNG/512/x_icon_150997.png"
                            alt="fechar(x)"
                            className="w-[40px] h-[40px] cursor-pointer"
                        />
                    </button>
                </div>
                <h2 className="text-3xl text-center">Compartilhar</h2>
                <div className="flex flex-col gap-2.5 overflow-hidden">
                    <Share
                        visibility={manager.video!.visibility}
                        changeVisibility={manager.changeVisibility}
                        changeCanAnotate={manager.changeCanAnotate}
                        isChangingVisibility={asyncAction === "video-user-privileges"}  
                        removeUser={manager.removeUser}                      
                    />
                </div>
                <div className="flex gap-4">
                    {manager.video!.visibility.type === VisibilityType.RESTRICTED &&
                        <button
                            onClick={() => setVisibleModal("add")}
                            className="bg-orange-400 px-2.5 py-0.5 rounded-2xl text-xl cursor-pointer hover:bg-orange-300"
                        >
                            Adicionar Usuario
                        </button>
                    }
                    {manager.video!.visibility.type === VisibilityType.PUBLIC &&
                        <button
                            onClick={e => {
                                e.stopPropagation();
                                const div = e.target as HTMLDivElement;

                                const origin = window.location.origin;
                                const userLogin = manager.video!.ownerName;
                                const videoId = manager.video!.id;

                                const completeLink = `${origin}/user/${userLogin}/video/${videoId}`;

                                navigator.clipboard.writeText(completeLink)
                                    .then(() => {
                                        return new Promise((r) => {
                                            div.style.transition = "background-color linear 0.2s";
                                            div.style.backgroundColor = "#9ae600";
                                            setTimeout(r, 200);
                                        });
                                    })
                                    .then(() => {
                                        div.style.backgroundColor = "";
                                        setTimeout(() => {
                                            div.style.transition = "";
                                        }, 200);
                                    })
                                    .catch(() => alert("Não foi possível copiar o link.\nLink: " + completeLink));
                            }}
                            className="bg-orange-400 px-2.5 py-0.5 rounded-2xl text-xl cursor-pointer hover:bg-orange-300"
                        >
                            Copiar Link
                        </button>
                    }
                    <button
                        onClick={() => setVisibleModal("edit")}
                        className="bg-orange-400 px-2.5 py-0.5 rounded-2xl text-xl cursor-pointer hover:bg-orange-300"
                    >
                        Editar
                    </button>
                    <button
                        onClick={() => setVisibleModal("delete")}
                        className="bg-red-600 px-2.5 py-0.5 rounded-2xl text-xl cursor-pointer hover:bg-red-500"
                    >
                        Deletar
                    </button>
                </div>
            </div>
            <div
                id="video-options-modal-add"
                className={`absolute bg-[#ffffdb] h-[58%] w-[65%] p-4 flex flex-col items-center justify-center gap-6 ${visibleModal === "add" ? "opacity-100 z-1" : "opacity-0 -z-1"} rounded-3xl`}
            >
                {argolas.amount.map((_, i) =>
                    <Argola
                        key={i}
                        radio={40}
                        x={i*29 + argolas.x + 45}
                        y={argolas.y}
                    />
                )}
                <h2 className="text-2xl">Adicionar um novo usuário</h2>
                <AddUser
                    findUsers={manager.findUsers}
                    selectUser={(name: string) => userRef.current = name}
                    isVisible={visibleModal === "add"}
                />
                <div className="flex gap-4">
                    <button
                        className={`${asyncAction === "video-edit" ? "bg-gray-400 cursor-wait" : "bg-green-600 cursor-pointer"} px-2.5 py-0.5 rounded-2xl text-xl hover:scale-110`}
                        onClick={() => asyncAction === undefined && manager.addUser(userRef.current)}
                    >
                        Adicionar
                    </button>
                    <button
                        onClick={() => asyncAction === undefined && setVisibleModal("info")}
                        className="bg-red-600 cursor-pointer px-2.5 py-0.5 rounded-2xl text-xl hover:scale-110"
                    >
                        Cancelar
                    </button>
                </div>
            </div>
            <div
                id="video-options-modal-edit"
                className={`absolute bg-[#ffffdb] h-[58%] w-[65%] p-4 flex flex-col items-center justify-center gap-6 ${visibleModal === "edit" ? "opacity-100 z-1" : "opacity-0 -z-1"} rounded-3xl`}
            >
                {argolas.amount.map((_, i) =>
                    <Argola
                        key={i}
                        radio={40}
                        x={i*29 + argolas.x + 45}
                        y={argolas.y}
                    />
                )}
                <h2 className="text-2xl">Editar</h2>
                <div className="flex flex-col w-full">
                    <label
                        htmlFor="title"
                        className="text-xl border-b border-red-600"
                    >
                        Título
                    </label>
                    <input
                        id="title"
                        type="text"
                        value={videoUpdate.title}
                        onChange={updateVideoUpdate}
                        className="text-xl border-b border-red-600 outline-none"
                    />
                </div>
                <div className="flex flex-col w-full">
                    <label
                        htmlFor="description"
                        className="text-xl border-b border-red-600"
                    >
                        Descrição
                    </label>
                    <textarea
                        id="description"
                        value={videoUpdate.description}
                        onChange={updateVideoUpdate}
                        placeholder=" "
                        className="text-xl underline decoration-red-600 underline-offset-4 outline-none resize-none placeholder-shown:border-b placeholder-shown:border-red-600 placeholder-shown:no-underline"
                    />
                </div>
                <div className="flex gap-4">
                    <button
                        className={`${asyncAction === "video-edit" ? "bg-gray-400 cursor-wait" : "bg-green-600 cursor-pointer"} px-2.5 py-0.5 rounded-2xl text-xl hover:scale-110`}
                        onClick={() => asyncAction === undefined && manager.edit(videoUpdate)}
                    >
                        Editar
                    </button>
                    <button
                        onClick={() => asyncAction === undefined && setVisibleModal("info")}
                        className="bg-red-600 cursor-pointer px-2.5 py-0.5 rounded-2xl text-xl hover:scale-110"
                    >
                        Cancelar
                    </button>
                </div>
            </div>
            <div
                id="video-options-modal-delete"
                className={`absolute bg-[#ffffdb] h-[58%] w-[65%] p-4 flex flex-col items-center justify-center gap-6 ${visibleModal === "delete" ? "opacity-100 z-1" : "opacity-0 -z-1"} rounded-3xl`}
            >
                {argolas.amount.map((_, i) =>
                    <Argola
                        key={i}
                        radio={40}
                        x={i*29 + argolas.x + 45}
                        y={argolas.y}
                    />
                )}
                <span className="text-2xl">Você tem certeza que quer deletar este vídeo?</span>
                <div className="flex gap-2">
                    <button
                        className={`${asyncAction === "video-delete" ? "bg-gray-400 cursor-wait" : "bg-red-600 cursor-pointer"} px-2.5 py-0.5 rounded-2xl text-xl hover:scale-110`}
                        onClick={() => asyncAction === undefined && manager.deleteVideo()}
                    >
                        Deletar
                    </button>
                    <button
                        className="bg-green-600 cursor-pointer px-2.5 py-0.5 rounded-2xl text-xl hover:scale-110"
                        onClick={() => asyncAction === undefined && setVisibleModal("info")}
                    >
                        Cancelar
                    </button>
                </div>
            </div>
        </div>
    );
}