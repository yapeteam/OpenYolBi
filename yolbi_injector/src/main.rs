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

    println!("resources released at: {}", yolbi_path.display());
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
        println!("©2024 YapeTeam, 保留所有权利。");
        println!("作者：TIMER_err及YolBi Lite项目的所有贡献者");
        println!("项目仓库：github.com/yapeteam/OpenYolBi");
        println!("使用本软件即表示您已阅读并同意遵守Minecraft最终用户许可协议:");
        println!("https://www.minecraft.net/zh-hans/eula");
        println!("YapeTeam对于因违反许可协议而产生的任何后果不承担责任。");
        println!("本软件承诺永久免费，请您确保从合法的免费渠道获得。");
        println!("如是1.18.1请使用浏览器打开localhost:23333调参");
        if print.is_ok() {
            let symbol: Symbol<fn()> = print.unwrap();
            symbol();
        } else {
            return;
        }

        print!("pid:");
        io::stdout().flush().unwrap();

        let mut input = String::new();
        if io::stdin().read_line(&mut input).is_err() {
            println!("unexpected error");
            return;
        }

        let input: c_int = input.trim().parse().expect("not a number");

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