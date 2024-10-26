use indicatif::{ProgressBar, ProgressStyle};
use std::io::{self, Read, Write};
use std::net::{Shutdown, TcpListener, TcpStream};
use std::str;

pub(crate) fn start() -> Result<(), Box<dyn std::error::Error>> {
    let listener = TcpListener::bind("127.0.0.1:20181")?;

    println!("Server started on {}", listener.local_addr()?);

    for stream in listener.incoming() {
        match stream {
            Ok(stream) => {
                println!("Client connected!");
                handle_client(stream)?;
                println!("Client disconnected!");
                break; // 只处理一个客户端
            }
            Err(e) => {
                eprintln!("Connection failed: {}", e);
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
                println!("Connection closed by the client.");
                break;
            }
            prediction_size => {
                let mut message = str::from_utf8(&buffer[..prediction_size]).unwrap().trim_end_matches("\r\n");
                let head = if (message.len() > 3) { &message[0..2] } else { message };
                let body = if (message.contains("=>")) { &message[4..] } else { message };

                match head {
                    "S1" => {
                        progress_bar.reset();
                        progress_bar.set_message("mapping");
                    }
                    "S2" => {
                        progress_bar.reset();
                        progress_bar.set_message("transformation");
                    }
                    "P1" => {
                        if let Ok(value) = body.parse::<f32>() {
                            next = value as u64;
                        }
                    }
                    "P2" => {
                        if let Ok(value) = body.parse::<f32>() {
                            next = value as u64;
                        }
                    }
                    "E1" => {
                        progress_bar.finish_and_clear();
                    }
                    "E2" => {
                        progress_bar.finish_and_clear();
                    }
                    "LG" => {
                        if let Ok(value) = body.parse::<String>() {
                            print!("\x1b[2K\r{}\n", value.trim_end_matches('\n'));
                            progress_bar.set_position(next)
                        }
                    }
                    "ED" => {
                        println!("INJECT SUCCESSFULLY");
                        break;
                    }
                    _ => {}
                }
            }
        }
    }
    println!("Press any key to exit...");
    io::stdout().flush()?;
    let mut input = [0u8; 1];
    if let Ok(_) = io::stdin().read(&mut input) {
        println!("Exiting...");
    }

    stream.shutdown(Shutdown::Both)?;
    Ok(())
}
