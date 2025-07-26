export default function Argola({
    radio,
    x,
    y
}: {
    radio: number,
    x: number,
    y: number
}) {
    return <>
        <div
            className="border-4 border-gray-400 border-b-transparent rounded-full absolute"
            style={{
                transform: `translate(${x}px, ${y}px) rotate(45deg)`,
                width: radio + "px",
                height: radio + "px"
            }}
        />
    </>
}