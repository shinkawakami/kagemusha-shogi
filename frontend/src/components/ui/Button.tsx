type Props = {
  children: React.ReactNode
  onClick?: () => void
  variant?: 'primary' | 'selectable'
  selected?: boolean
  className?: string
}

export default function Button({
  children,
  onClick,
  variant = 'primary',
  selected = false,
  className,
}: Props) {

  const baseStyle = `
    px-8 py-4
    rounded-2xl
    font-bold
    text-lg
    transition-all
    shadow-2xl
    hover:scale-105
  `

  const variantStyle = {
    primary:
      'bg-amber-400 text-black',

    selectable: selected
      ? 'bg-white text-black'
      : 'bg-zinc-800 text-white border border-zinc-700',
  }

  return (
    <button
      onClick={onClick}
      className={`
        ${baseStyle}
        ${variantStyle[variant]}
        ${className}
      `}
    >
      {children}
    </button>
  )
}