function _toast() {
    const toast = document.createElement('div');
    toast.style.position = 'fixed';
    toast.style.bottom = '20px';
    toast.style.left = '50%';
    toast.style.transform = 'translateX(-50%)';
    toast.style.padding = '8px 20px';
    toast.style.borderRadius = '16px';
    toast.style.fontSize = '16px';
    toast.style.fontFamily = 'inherit';
    toast.style.backdropFilter = 'blur(8px)';
    toast.style.zIndex = '1000';
    toast.style.boxShadow = '0 4px 12px rgba(0,0,0,0.3)';
    toast.style.letterSpacing = '1px';
    toast.style.transition = 'all 0.2s'

    document.body.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 500);
    }, 2200);

    return toast;
}

function info(msg: string) {
    const toast = _toast()
    toast.innerText = msg;
    toast.style.backgroundColor = 'var(--button-hover-bg)';
    toast.style.border = '1px solid var(--button-hover-border)';
    toast.style.color = 'var(--text-h)';
}

function error(msg: string) {
    const toast = _toast();
    toast.innerText = msg;
    toast.style.backgroundColor = 'var(--button-active-bg)';
    toast.style.border = '1px solid var(--button-active-border)';
    toast.style.color = 'var(--text-h)';
}

export default { info, error }