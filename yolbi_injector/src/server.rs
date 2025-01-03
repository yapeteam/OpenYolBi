use indicatif::{ProgressBar, ProgressStyle};
use std::io::{self, Read, Write};
use std::net::{Shutdown, TcpListener, TcpStream};
use std::str;

pub(crate) fn start() -> Result<(), Box<dyn std::error::Error>> {
    let listener = TcpListener::bind("127.0.0.1:20181")?;

    println!("注射打开于 {}", listener.local_addr()?);

    for stream in listener.incoming() {
        match stream {
            Ok(stream) => {
                println!("客户端链接");
                handle_client(stream)?;
                println!("客户端已注射");
                break;
            }
            Err(e) => {
                eprintln!("连接失败: {}", e);
                continue;
            }
        }
    }
    Ok(())
}

fn handle_client(mut stream: TcpStream) -> io::Result<()> {
    let mut buffer = [0u8; 1024];
    let progress_bar = ProgressBar::new(100);
    progress_bar.set_style(ProgressStyle::default_bar()
        .template("{spinner:.green} [{elapsed_precise}] [{wide_bar:.white}] {pos:>7}/{len:7} {percent:>7}%").unwrap()
    );

    let mut next: u64 = 0;
    loop {
        match stream.read(&mut buffer)? {
            0 => {
                println!("来自客户端的链接关闭");
                break;
            }
            prediction_size => {
                let message = str::from_utf8(&buffer[..prediction_size]).unwrap();
                let mut values: Vec<&str> = message.split("=>").collect();
                values[0] = values[0].trim_end_matches("\r\n");
                if values.len() > 1 { values[1] = values[1].trim_end_matches("\r\n"); }
                match values[0] {
                    "S1" => {
                        progress_bar.reset();
                        progress_bar.set_message("编写地图");
                        println!("编写地图中");
                    }
                    "S2" => {
                        progress_bar.reset();
                        progress_bar.set_message("重定向");
                        println!("重定向中")
                    }
                    "P1" => {
                        if let Ok(value) = values[1].parse::<f32>() {
                            next = value as u64;
                        }
                    }
                    "P2" => {
                        if let Ok(value) = values[1].parse::<f32>() {
                            next = value as u64;
                        }
                    }
                    "E1" => {
                        progress_bar.finish_and_clear();
                    }
                    "E2" => {
                        progress_bar.finish_and_clear();
                    }
                    "LOG" => {
                        if let Ok(value) = values[1].parse::<String>() {
                            print!("\x1b[2K\r{}\n", value.trim_end_matches('\n'));
                            progress_bar.set_position(next)
                        }
                    }
                    "CLOSE" => {
                        println!("注射成功");
                        break;
                    }
                    _ => {}
                }
            }
        }
    }

    stream.shutdown(Shutdown::Both)?;
    Ok(())
}
