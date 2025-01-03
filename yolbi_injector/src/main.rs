mod server;

use libc::wchar_t;
use libloading::{Library, Symbol};
use std::ffi::c_int;
use std::ffi::OsStr;
use std::io::{Cursor, Write};
use std::os::windows::ffi::OsStrExt;
use std::path::{Path, PathBuf};
use std::{env, io};
use zip::result::ZipError;
use zip::ZipArchive;

fn release_resources(yolbi_path: PathBuf) -> Result<(), ZipError> {
    let included_zip = include_bytes!("injection.zip");
    std::fs::create_dir_all(&yolbi_path)?;

    let mut zip = ZipArchive::new(Cursor::new(included_zip))?;
    zip.extract(&yolbi_path)?;

    println!("文件储存在: {}", yolbi_path.display());
    Ok(())
}

fn main() {
    unsafe {
        let user_dir = env::home_dir().unwrap().to_str().unwrap().to_owned();
        let user_path = Path::new(&user_dir);
        let yolbi_path = user_path.join(".yolbi");
        release_resources(yolbi_path.clone()).unwrap();

        let lib = Library::new(yolbi_path.join("libapi.dll")).unwrap();
        let print = lib.get(b"printMinecraftProcesses\0");

        println!("{}",
                 format!("{}\n{}\n{}\n{}\n{}\n{}\n",
                         "██╗   ██╗ ██████╗ ██╗     ██████╗ ██╗    ██╗     ██╗████████╗███████╗",
                         "╚██╗ ██╔╝██╔═══██╗██║     ██╔══██╗██║    ██║     ██║╚══██╔══╝██╔════╝",
                         " ╚████╔╝ ██║   ██║██║     ██████╔╝██║    ██║     ██║   ██║   █████╗  ",
                         "  ╚██╔╝  ██║   ██║██║     ██╔══██╗██║    ██║     ██║   ██║   ██╔══╝  ",
                         "   ██║   ╚██████╔╝███████╗██████╔╝██║    ███████╗██║   ██║   ███████╗",
                         "   ╚═╝    ╚═════╝ ╚══════╝╚═════╝ ╚═╝    ╚══════╝╚═╝   ╚═╝   ╚══════╝"
                 )
        );
        println!("ReBuild 0.4.0 #0037");
        println!("您在使用本工具做的任何事情我们YolbiTeam不承担任何责任");
        println!("我们的YOLBI LITE public 是免费的此工具版本为public如遇到付费购买请退货");
        println!("提示1.18.1打开调参需要再浏览器打开网页>>http://localhost:23333/<<");
        println!("©YolbiTeam");
        if print.is_ok() {
            let symbol: Symbol<fn()> = print.unwrap();
            symbol();
        } else {
            return;
        }

        print!("请输入上文pid提供的数字请启动游戏才会有: ");
        io::stdout().flush().unwrap();

        let mut input = String::new();
        if io::stdin().read_line(&mut input).is_err() {
            println!("致命错误 #0");
            return;
        }

        let input: c_int = input.trim().parse().expect("密码是数字吗");

        let inject = lib.get(b"Inject\0");
        if inject.is_ok() {
            let symbol: Symbol<fn(c_int, *const wchar_t)> = inject.unwrap();
            let dll_path = yolbi_path.join("libinjection.dll").to_str().unwrap().to_owned();
            let rust_str = format!("{}\0", dll_path);
            let wide_str: Vec<u16> = OsStr::new(&rust_str).encode_wide().collect();
            let wide_c_str = wide_str.as_ptr();
            symbol(input, wide_c_str);
        }
        let _ = server::start();
    }
}
