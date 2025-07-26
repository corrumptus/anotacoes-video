import { useRouter } from "next/navigation";
import Argola from "@components/utils/Argola";
import { Video } from "@hooks/useVideoStore";

export default function VideoList({
    isLoading,
    videos
}: {
    isLoading: boolean,
    videos: Video[]
}) {
    const router = useRouter();

    if (isLoading) return (
        <div className="h-full flex items-center justify-center text-3xl">
            Loading...
        </div>
    );

    if (videos.length === 0) return (
        <div className="h-full flex items-center justify-center text-3xl">
            Vazio
        </div>
    );

    return (
        <ul className="h-full w-full flex flex-wrap gap-4 gap-y-8 overflow-auto p-3">
            {videos.map(v =>
                <li
                    key={v.id}
                    className="bg-[#ffffbd] p-4 pb-3 h-[146px] w-[192px] rounded-3xl flex flex-col gap-1 items-center cursor-pointer relative"
                    onClick={() => router.push("/video/" + v.id)}
                >
                    {new Array(Math.floor(192/22)).fill(undefined)
                        .map((_, i) => <Argola key={i} radio={24} x={i*18.5 - 64} y={-28} />)
                    }
                    <div className="flex justify-center items-center cursor-pointer overflow-hidden">
                        <img
                            src={v.thumb}
                            className=" cursor-pointer"
                        />
                    </div>
                    <h2 className="w-full overflow-clip whitespace-nowrap text-ellipsis cursor-pointer border-y border-red-600">
                        {v.title}
                    </h2>
                </li>
            )}
        </ul>
    );
}