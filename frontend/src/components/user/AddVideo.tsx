import { useState, ChangeEvent, useEffect } from "react";
import Argola from "@components/utils/Argola";
import { NewVideo } from "@hooks/useVideoStore";

export default function AddVideo({
    isVisible,
    close,
    add,
    isAdding
}: {
    isVisible: boolean,
    close: () => void,
    add: (newVideo: NewVideo) => void,
    isAdding: boolean
}) {
    const [ argolas, setArgolas ] = useState<undefined[]>([]);
    const [ newVideo, setNewVideo ] = useState<{
        title: string,
        description: string,
        video: File | null
    }>({ title: "", description: "", video: null });

    useEffect(() => {
        function onResize() {
            setArgolas(new Array(Math.floor(document.querySelector("#add-video-modal-parent-element div")!.clientWidth/30)).fill(undefined));
        }

        window.addEventListener("resize", onResize);

        onResize();

        return () => {
            window.removeEventListener("resize", onResize);
        }
    }, []);

    useEffect(() => {
        if (!isAdding)
            close();
    }, [isAdding]);

    useEffect(() => {
        if (!isVisible)
            setNewVideo({ title: "", description: "", video: null });
    }, [isVisible]);

    function changeNewVideo(e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        if (isAdding)
            return;

        if (e.target.id === "file") {
            setNewVideo(cur => ({...cur, video: (e.target as HTMLInputElement).files![0]}));
            return;
        }

        setNewVideo(cur => ({...cur, [e.target.id]: e.target.value}));
    }

    function adicionar() {
        if (newVideo.video === null) {
            alert("Selecione um vídeo");
            return;
        }

        add(newVideo as NewVideo);
    }

    return (
        <div
            id="add-video-modal-parent-element"
            className={`absolute top-0 h-full w-full bg-black/50 flex items-center justify-center ${isVisible ? "opacity-100 z-1" : "opacity-0 -z-1"}`}
            onClick={e => {
                e.stopPropagation();

                if ((e.target as HTMLDivElement).id !== "add-video-modal-parent-element")
                    return;

                close();
            }}
        >
            <div className="h-[60%] w-[60%] bg-[#ffffbd] flex flex-col items-stretch justify-center gap-2 p-6 rounded-3xl">
                {argolas.map((_, i) =>
                        <Argola
                            key={i}
                            radio={40}
                            x={i*29 - 4}
                            y={-216}
                        />
                    )
                }
                <div className="absolute top-[calc(20%_+_var(--spacing)_*_6)] right-[calc(20%_+_var(--spacing)_*_6)]">
                    <button onClick={close}>
                        <img
                            src="https://images.icon-icons.com/2518/PNG/512/x_icon_150997.png"
                            alt="fechar(x)"
                            className="w-[40px] h-[40px] cursor-pointer"
                        />
                    </button>
                </div>
                <header className="text-2xl font-bold text-center">
                    Adicione um novo vídeo
                </header>
                <div className="flex flex-col gap-1">
                    <label
                        htmlFor="title"
                        className="text-xl border-b border-red-600"
                    >
                        Título
                    </label>
                    <input
                        id="title"
                        type="text"
                        value={newVideo.title}
                        onChange={changeNewVideo}
                        className="border-b border-red-600 outline-none"
                    />
                </div>
                <div className="flex flex-col gap-1">
                    <label
                        htmlFor="description"
                        className="text-xl border-b border-red-600"
                    >
                        Descrição
                    </label>
                    <textarea
                        id="description"
                        placeholder=" "
                        value={newVideo.description}
                        onChange={changeNewVideo}
                        className="underline decoration-red-600 underline-offset-4 outline-none resize-none placeholder-shown:border-b placeholder-shown:border-red-600 placeholder-shown:no-underline"
                    />
                </div>
                <div className="flex flex-col gap-1 border-b border-red-600">
                    <label
                        htmlFor="file"
                        className="text-xl border-b border-red-600"
                    >
                        Vídeo
                    </label>
                    <input
                        id="file"
                        type="file"
                        accept=".mp4, .webm"
                        onChange={changeNewVideo}
                        className="outline-none cursor-pointer mb-1 px-2 rounded-full bg-yellow-300"
                    />
                </div>
                <div className="border-b border-red-600 pb-1">
                    <button
                        className={`${isAdding ? "bg-gray-600 cursor-wait" : "bg-[#89ffff] cursor-pointer"} rounded-2xl px-2 py-0.5 transition-colors transition-700`}
                        onClick={adicionar}
                    >
                        Adicionar
                    </button>
                </div>
            </div>
        </div>
    );
}