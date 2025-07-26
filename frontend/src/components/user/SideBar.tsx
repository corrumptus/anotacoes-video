import { SelectedPage } from "@hooks/useVideoStore";

export default function SideNavBar({
    selectedPage,
    changeSelectedPage,
    isColapsed,
    switchColapse
}: {
    selectedPage: SelectedPage,
    changeSelectedPage: (selectedPage: SelectedPage) => void,
    isColapsed: boolean,
    switchColapse: () => void
}) {
    return (
        <nav className={`h-full border-r border-black transition-all duration-800 ease-linear ${isColapsed ? "w-[50px]" : "w-[206px]"}`}>
            <header className="flex flex-row-reverse">
                <button
                    className={`${isColapsed ? "rotate-180" : "rotate-0"} transition-transform duration-800 ease-linear cursor-pointer`}
                    onClick={switchColapse}
                >
                    <img
                        src="https://cdn3.iconfinder.com/data/icons/arrows-219/24/collapse-left-1024.png"
                        alt="retrair(seta apontando para a esquerda)"
                        className="w-[40px] h-[40px]"
                    />
                </button>
            </header>
            <ul className="p-0 overflow-hidden">
                <li
                    className="border-b border-red-600 p-1"
                    onClick={() => changeSelectedPage(SelectedPage.MEUS_VIDEOS)}
                >
                    <div className={`relative flex items-center ${selectedPage === SelectedPage.MEUS_VIDEOS ? "bg-gray-300 " : ""}rounded-full overflow-hidden cursor-pointer`}>
                        <button className={`${isColapsed ? "opacity-100 z-1" : "opacity-0"} transition-opacity duration-800 p-1 rounded-full cursor-pointer`}>
                            <img
                                src="https://img.icons8.com/?size=512&id=i6fZC6wuprSu&format=png"
                                alt="meus vídeos(casa)"
                                className="w-[30px] h-[30px] cursor-pointer -translate-y-[1px]"
                            />
                        </button>
                        <button className={`rounded-xl px-2 text-xl ${isColapsed ? "opacity-0" : "opacity-100 z-1"} transition-opacity duration-800 absolute cursor-pointer whitespace-nowrap`}>
                            Meus Vídeos
                        </button>
                    </div>
                </li>
                <li
                    className="border-b border-red-600 p-1"
                    onClick={() => changeSelectedPage(SelectedPage.COMPARTILHADOS)}
                >
                    <div className={`relative flex items-center ${selectedPage === SelectedPage.COMPARTILHADOS ? "bg-gray-300 " : ""}rounded-full overflow-hidden cursor-pointer`}>
                        <button className={`${isColapsed ? "opacity-100 z-1" : "opacity-0"} transition-opacity duration-800 p-1 rounded-full cursor-pointer`}>
                            <img
                                src="https://www.freeiconspng.com/uploads/black-circle-social-media-share-icon-png-25.png"
                                alt="compartilhar(2 pontos conectados a outro formando um triangulo não fechado)"
                                className="w-[30px] h-[30px] cursor-pointer"
                            />
                        </button>
                        <button className={`rounded-xl px-2 text-xl ${isColapsed ? "opacity-0" : "opacity-100 z-1"} transition-opacity duration-800 absolute cursor-pointer`}>
                            Compartilhados
                        </button>
                    </div>
                </li>
                <li
                    className="border-b border-red-600 p-1"
                    onClick={() => changeSelectedPage(SelectedPage.PUBLICOS)}
                >
                    <div className={`relative flex items-center ${selectedPage === SelectedPage.PUBLICOS ? "bg-gray-300 " : ""}rounded-full overflow-hidden cursor-pointer`}>
                        <button className={`${isColapsed ? "opacity-100 z-1" : "opacity-0"} transition-opacity duration-800 p-1 rounded-full cursor-pointer`}>
                            <img
                                src="https://png.pngtree.com/png-vector/20230315/ourmid/pngtree-globe-line-icon-vector-png-image_6650542.png"
                                alt="públicos(mundo)"
                                className="w-[30px] h-[30px] cursor-pointer"
                            />
                        </button>
                        <button className={`rounded-xl px-2 text-xl ${isColapsed ? "opacity-0" : "opacity-100 z-1"} transition-opacity duration-800 absolute cursor-pointer`}>
                            Públicos
                        </button>
                    </div>
                </li>
            </ul>
        </nav>
    );
}